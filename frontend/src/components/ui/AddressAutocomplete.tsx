import { useState, useEffect, useRef } from 'react';
import { MapPin, Loader2 } from 'lucide-react';

interface AddressSuggestion {
  formatted: string;
  housenumber?: string;
  street?: string;
  city?: string;
  state?: string;
  postcode?: string;
  country?: string;
  country_code?: string;
  address_line1?: string;
  address_line2?: string;
}

interface AddressAutocompleteProps {
  value: string;
  onChange: (value: string) => void;
  onSelect: (address: {
    address: string;
    city: string;
    postalCode: string;
    country: string;
  }) => void;
  label?: string;
  error?: string;
  placeholder?: string;
}

export const AddressAutocomplete = ({
  value,
  onChange,
  onSelect,
  label = 'Address',
  error,
  placeholder = 'Start typing your address...',
}: AddressAutocompleteProps) => {
  const [suggestions, setSuggestions] = useState<AddressSuggestion[]>([]);
  const [isOpen, setIsOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const wrapperRef = useRef<HTMLDivElement>(null);
  const apiKey = import.meta.env.VITE_GEOAPIFY_KEY;

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target as Node)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const fetchSuggestions = async (text: string) => {
    if (!apiKey || text.length < 3) {
      setSuggestions([]);
      return;
    }

    setIsLoading(true);
    try {
      const res = await fetch(
        `https://api.geoapify.com/v1/geocode/autocomplete?text=${encodeURIComponent(text)}&format=json&apiKey=${apiKey}`
      );
      const data = await res.json();
      if (data.results) {
        setSuggestions(data.results.slice(0, 5));
        setIsOpen(true);
      }
    } catch (err) {
      console.error('Geoapify autocomplete error:', err);
      setSuggestions([]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value;
    onChange(val);

    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      fetchSuggestions(val);
    }, 300);
  };

  const handleSelect = (suggestion: AddressSuggestion) => {
    const addressLine = suggestion.address_line1 || 
      [suggestion.housenumber, suggestion.street].filter(Boolean).join(' ') || 
      suggestion.formatted;

    onChange(addressLine);
    onSelect({
      address: addressLine,
      city: suggestion.city || '',
      postalCode: suggestion.postcode || '',
      country: suggestion.country_code?.toUpperCase() || suggestion.country || '',
    });
    setIsOpen(false);
    setSuggestions([]);
  };

  return (
    <div className="flex flex-col gap-1.5 w-full relative" ref={wrapperRef}>
      {label && (
        <label className="text-sm font-bold" style={{ color: 'var(--text-secondary)' }}>
          {label}
        </label>
      )}
      <div className="relative">
        <input
          type="text"
          value={value}
          onChange={handleInputChange}
          onFocus={() => suggestions.length > 0 && setIsOpen(true)}
          placeholder={placeholder}
          className={`flex h-12 w-full rounded-lg border px-3 pl-10 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent disabled:cursor-not-allowed disabled:opacity-50 transition-all ${
            error ? 'border-red-500 focus:ring-red-500' : ''
          }`}
          style={{
            backgroundColor: 'var(--input-bg)',
            borderColor: error ? undefined : 'var(--border-color)',
            color: 'var(--text-primary)',
          }}
        />
        <div className="absolute left-3 top-1/2 -translate-y-1/2">
          {isLoading ? (
            <Loader2 className="w-4 h-4 animate-spin" style={{ color: 'var(--text-muted)' }} />
          ) : (
            <MapPin className="w-4 h-4" style={{ color: 'var(--text-muted)' }} />
          )}
        </div>
      </div>

      {error && <span className="text-xs text-red-500 font-medium">{error}</span>}

      {isOpen && suggestions.length > 0 && (
        <div
          className="absolute top-full left-0 right-0 mt-1 z-50 rounded-xl border shadow-xl overflow-hidden max-h-60 overflow-y-auto"
          style={{ backgroundColor: 'var(--surface)', borderColor: 'var(--border-color)' }}
        >
          {suggestions.map((s, idx) => (
            <button
              key={idx}
              type="button"
              onClick={() => handleSelect(s)}
              className="w-full text-left px-4 py-3 flex items-start gap-3 transition-colors border-b last:border-0 hover:bg-primary/5"
              style={{ borderColor: 'var(--border-subtle)' }}
            >
              <MapPin className="w-4 h-4 mt-0.5 shrink-0 text-primary" />
              <div>
                <p className="text-sm font-medium" style={{ color: 'var(--text-primary)' }}>
                  {s.address_line1 || [s.housenumber, s.street].filter(Boolean).join(' ') || s.formatted}
                </p>
                <p className="text-xs mt-0.5" style={{ color: 'var(--text-muted)' }}>
                  {[s.city, s.state, s.postcode, s.country].filter(Boolean).join(', ')}
                </p>
              </div>
            </button>
          ))}
        </div>
      )}

      {!apiKey && (
        <p className="text-xs mt-1" style={{ color: 'var(--text-muted)' }}>
          💡 Add VITE_GEOAPIFY_KEY to .env for address autocomplete
        </p>
      )}
    </div>
  );
};
